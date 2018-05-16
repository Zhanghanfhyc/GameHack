package com.fossgalaxy.games.tbs.ai;

import com.fossgalaxy.games.tbs.ai.rules.PerEntityRule;
import com.fossgalaxy.games.tbs.ai.rules.ProductionRule;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.ui.GameAction;
import com.fossgalaxy.object.ObjectFinder;
import com.google.common.reflect.TypeToken;
import rts.ai.core.AI;
import rts.ai.evaluation.EvaluationFunction;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AIFactory {

    // object finders
    private ObjectFinder<EvaluationFunction> evaluationFunctionFinder;
    private ObjectFinder<ProductionRule> productionRuleFinder;
    private ObjectFinder<Controller> controllerFinder;

    // indirection maps
    private final Map<String, String> evaluationFunctions;
    private final Map<String, String> productionDefinitions;
    private final Map<String, String> controllerDefinitions;

    //current game paramters
    private GameSettings settings;
    private SettingsIO io;


    public AIFactory(SettingsIO io, String evalFileName, String rulesFileName, String aiFileName){
    	this.io = io;
        this.evaluationFunctions = new HashMap<>();
        this.productionDefinitions = new HashMap<>();
        this.controllerDefinitions = new HashMap<>();

        buildFinders(evalFileName, rulesFileName, aiFileName);
    }

    public void buildFinders(String evalFileName, String rulesFileName, String aiFileName) {
        io.loadAliases(evalFileName, evaluationFunctions);
        evaluationFunctionFinder = buildFinder(EvaluationFunction.class, evaluationFunctions);

        io.loadAliases(rulesFileName, productionDefinitions);
        productionRuleFinder = buildFinder(ProductionRule.class, productionDefinitions);

        io.loadAliases(aiFileName, controllerDefinitions);
        controllerFinder = buildFinder(Controller.class, controllerDefinitions);

        // special converters
        productionRuleFinder.addConverter(PerEntityRule.class, x -> (PerEntityRule)productionRuleFinder.buildObject(x));
        productionRuleFinder.addConverter(ProductionRule[].class, x -> Arrays.stream(x.split(",")).map(this::buildProductionRule).toArray(ProductionRule[]::new));
        controllerFinder.addConverter(EvaluationFunction.class, this::buildEvalFunction);
        controllerFinder.addConverter(AI.class, x -> (AI)controllerFinder.buildObject(x));

        System.out.println(controllerFinder.getInstalledConverters());
        System.out.println("controller types: "+controllerFinder.getBuildableObjects());

        controllerFinder.addConverter(ProductionRule[].class, x -> Arrays.stream(x.split(",")).map(this::buildProductionRule).toArray(ProductionRule[]::new));

    }

    /**
     * Utility method to support generic arrays of objects.
     *
     * @param array the array as a comma seperated string
     * @param converter the converter to use for making the object
     * @param <T> the resulting object type
     * @return an array of the created type
     */
    private static <T> T[] convArray(String array, Function<String, T> converter) {
        return Arrays.stream(array.split(",")).map(converter).toArray(t -> (T[])new Object[t]);
    }

    /**
     * Function to build a finder.
     *
     *
     * @param clazz the class this finder will be for
     * @param aliases the aliases this finder should be aware of
     * @param <T> The type of the loader
     * @return a loader, for use with the settings
     */
    private <T> ObjectFinder<T> buildFinder(Class<T> clazz, Map<String, String> aliases) {
        ObjectFinder<T> finder = new ObjectFinder.Builder<>(clazz)
                .addPackage(io.getPackageList())
                //.scanNow()
                .build();

        //register common types
        finder.addConverter(EntityType.class, this::getEntityType);
        finder.addConverter(ResourceType.class, this::getResourceType);
        finder.addConverter(GameAction.class, this::getAction);

        finder.addConverter(EntityType[].class, x -> Arrays.stream(x.split(",")).map(this::getEntityType).toArray(EntityType[]::new));

        //recursive loader with aliasing
        System.out.println(clazz);
        finder.addConverter(clazz, e -> {
        	System.out.println("trying to find out how to build an "+e);
        	return finder.buildObject(aliases.getOrDefault(e, e));
        });

        return finder;
    }

    public EvaluationFunction buildEvalFunction(String name) {
        String rewrite = evaluationFunctions.getOrDefault(name, name);
        return evaluationFunctionFinder.buildObject(rewrite);
    }

    public ProductionRule buildProductionRule(String name) {
        String rewrite = productionDefinitions.getOrDefault(name, name);
        return productionRuleFinder.buildObject(rewrite);
    }


    private GameAction getAction(String actionDef) {
        if (io == null){
            throw new IllegalArgumentException("IO is currently not defined!");
        }

        return io.convertAction(actionDef, settings);
    }

    private EntityType getEntityType(String name) {
        if (settings == null){
            throw new IllegalArgumentException("settings is currently not defined!");
        }

        return settings.getEntityType(name);
    }

    private ResourceType getResourceType(String name){
        if (settings == null){
            throw new IllegalArgumentException("settings is currently not defined!");
        }

        return settings.getResourceType(name);
    }

    public synchronized Controller buildAI(String name, GameSettings settings) {
        this.settings = settings;
        return controllerFinder.buildObject(controllerDefinitions.get(name));
    }

}
