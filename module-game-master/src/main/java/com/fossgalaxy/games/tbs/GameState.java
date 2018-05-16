package com.fossgalaxy.games.tbs;

import com.fossgalaxy.games.tbs.entity.*;
import com.fossgalaxy.games.tbs.io.map.MapData;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.HexagonalGrid;
import org.codetome.hexameter.core.api.HexagonalGridCalculator;
import org.codetome.hexameter.core.internal.GridData;
import rx.Observable;
import rx.functions.Action1;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Created by webpigeon on 13/10/17.
 */
public class GameState {
    private HexagonalGrid<HexagonTile> grid;
    private HexagonalGridCalculator<HexagonTile> calc;

    private int width;
    private int height;
    private int numberOfPlayers;

    private final GameSettings settings;

    private ArrayList<Entity> entities;
    private Map<Integer, List<Entity>> teamMap;

    private ArrayList<Resource> resources;

    private Map<Integer, Map<ResourceType, Integer>> playerResources;
    private CubeCoordinate[] startPositions;

    private int time;

    public GameState(int width, int height, GameSettings settings, int hexSize, int numberOfPlayers) {
        this.grid = HexUtils.buildGrid(width, height, hexSize).build();
        this.calc = HexUtils.buildGrid(width, height, hexSize).buildCalculatorFor(grid);

        this.settings = settings;
        this.time = 0;

        this.entities = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.playerResources = new HashMap<>();
        this.numberOfPlayers = numberOfPlayers;
        this.startPositions = new CubeCoordinate[]{
                CubeCoordinate.fromCoordinates(0, 0),
                CubeCoordinate.fromCoordinates(0, 0)
        };

        for (int i = 0; i < numberOfPlayers; i++) {
            playerResources.put(i, new HashMap<>());
        }

        this.teamMap = new HashMap<>();

        this.width = width;
        this.height = height;


        grid.getHexagons().forEach(h -> {
            h.setSatelliteData(new HexagonTile(Color.BLACK, "level_1", null));
        });
    }

    public GameState(GameState state) {
        this.width = state.width;
        this.height = state.height;
        this.time = state.time;

        //todo ASSUMPTION: grids are immutable
        this.grid = state.grid;
        this.calc = state.calc;

        this.entities = new ArrayList<>(state.entities.size());
        this.resources = new ArrayList<>(state.resources.size());

        this.numberOfPlayers = state.numberOfPlayers;
        this.settings = state.settings;

        this.playerResources = new HashMap<>();
        for (Map.Entry<Integer, Map<ResourceType, Integer>> entry : state.playerResources.entrySet()) {
            HashMap<ResourceType, Integer> pResource = new HashMap<>(entry.getValue());
            playerResources.put(entry.getKey(), pResource);
        }

        this.startPositions = state.startPositions;
        this.teamMap = new HashMap<>();

        for (Entity entity : state.entities) {
            Entity clone = new Entity(entity);
            entities.add(clone);

            List<Entity> e = teamMap.computeIfAbsent(clone.getOwner(), l -> new ArrayList<>());
            e.add(clone);
        }

        //todo ASSUMPTION: resources are immutable (not sure this is the case, as they have an amount.
        resources.addAll(state.resources);
    }

    public void tick() {
        time++;
    }

    public void addResource(int player, ResourceType type, int delta) {
        int amount = getResource(player, type) + delta;
        setResource(player, type, amount);
    }

    public void addResource(int player, String type, int delta) {
        ResourceType rt = settings.getResourceType(type);
        if (rt == null) {
            throw new IllegalArgumentException(String.format("Resource %s does not exist", type));
        }

        addResource(player, rt, delta);
    }

    public void setResource(int player, ResourceType type, int amount) {
        Map<ResourceType, Integer> resources = playerResources.get(player);
        if (resources == null) {
            throw new IllegalArgumentException("invalid player ID");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("you cannot have negative resources.");
        }

        resources.put(type, amount);
    }

    public int getResource(int player, ResourceType type) {
        Map<ResourceType, Integer> resources = playerResources.get(player);
        if (resources == null) {
            throw new IllegalArgumentException("invalid player ID");
        }

        return resources.getOrDefault(type, 0);
    }

    public int getResource(int player, String type) {
        ResourceType rt = settings.getResourceType(type);
        if (rt == null) {
            throw new IllegalArgumentException(String.format("invalid resource type %s", type));
        }

        return getResource(player, rt);
    }

    public Collection<Entity> getOwnedEntities(int owner) {
        return teamMap.getOrDefault(owner, Collections.emptyList());
    }

    public Collection<Entity> getEntities() {
        return Collections.unmodifiableCollection(entities);
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public int getDistance(CubeCoordinate src, CubeCoordinate dest) {
        Hexagon<HexagonTile> srcHex = cube2hex(src);
        Hexagon<HexagonTile> destHex = cube2hex(dest);

        return calc.calculateDistanceBetween(srcHex, destHex);
    }

    public Collection<Hexagon<HexagonTile>> getNeighbors(CubeCoordinate pos) {
        Hexagon<HexagonTile> tiles = cube2hex(pos);
        return grid.getNeighborsOf(tiles);
    }

    public Collection<Hexagon<HexagonTile>> getRange(CubeCoordinate pos, int range) {
        Hexagon<HexagonTile> tile = cube2hex(pos);
        return calc.calculateMovementRangeFrom(tile, range);
    }

    public void forAllHexagons(Action1<Hexagon<HexagonTile>> hexagonFunction) {
        Observable<Hexagon<HexagonTile>> hexagons = grid.getHexagons();
        hexagons.forEach(hexagonFunction);
    }

    public void forHexagonAt(CubeCoordinate c, Action1<Hexagon<HexagonTile>> function) {
        Hexagon<HexagonTile> hexagon = cube2hex(c);
        function.call(hexagon);
    }

    public <T> T computeHexagonAt(CubeCoordinate c, Function<Hexagon<HexagonTile>, T> function) {
        return function.apply(cube2hex(c));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        List<Entity> l = teamMap.computeIfAbsent(entity.getOwner(), k -> new ArrayList<>());
        l.add(entity);
    }

    public void addEntities(Collection<? extends Entity> entities) {
        entities.forEach(this::addEntity);
    }

    public Entity getEntityAt(CubeCoordinate vec) {
        for (Entity entity : entities) {
            if (vec.equals(entity.getPos())) {
                return entity;
            }
        }

        return null;
    }

    public Resource getResourceAt(CubeCoordinate pos) {
        for (Resource resource : resources) {
            if (pos.equals(resource.getLocation())) {
                return resource;
            }
        }

        return null;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void addResources(Collection<? extends Resource> resources) {
        this.resources.addAll(resources);
    }

    public CubeCoordinate pix2cube(java.awt.Point vec) {
        if (vec == null) {
            return null;
        }

        Hexagon<HexagonTile> hex = grid.getByPixelCoordinate(vec.x, vec.y).get();
        return hex.getCubeCoordinate();
    }

    public Hexagon<HexagonTile> cube2hex(CubeCoordinate cubeCoordinate) {
        org.codetome.hexameter.core.backport.Optional<Hexagon<HexagonTile>> oht = grid.getByCubeCoordinate(cubeCoordinate);
        if (!oht.isPresent()) {
            throw new IllegalArgumentException("could not find hexagon at: " + cubeCoordinate);
        }

        return oht.get();
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        teamMap.get(entity.getOwner()).remove(entity);
    }

    public Entity getEntityByID(UUID uuid) {

        //we need to avoid leaking references between versions.
        for (Entity entity : entities) {
            if (uuid.equals(entity.getID())) {
                return entity;
            }
        }

        return null;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getGridWidthPixels() {
        GridData data = grid.getGridData();
        return (int) ((data.getGridWidth() + 0.5) * data.getHexagonWidth());
    }

    public int getHexagonWidthPixels() {
        return (int) grid.getGridData().getHexagonWidth();
    }

    public int getGridHeightPixels() {
        GridData data = grid.getGridData();
        return (int) ((data.getHexagonHeight() / 2) + (data.getHexagonHeight() * height));
//        return (int) ((data.getGridHeight() * data.getHexagonHeight() / 2) * 1.75);
    }

    public int getHexagonHeightPixels() {
        return (int) grid.getGridData().getHexagonHeight();
    }

    public CubeCoordinate[] getStartPositions() {
        return startPositions;
    }

    public boolean isGameOver() {
        return false;
    }

    public int getTime() {
        return time;
    }

    public void addResourceAt(CubeCoordinate coordinate, ResourceType resourceType, int amountPerTurn) {

        //remove existing the resource, if present.
        resources.removeIf(resource -> coordinate.equals(resource.getLocation()));

        if (resourceType != null) {
            Resource resource = new Resource(resourceType, coordinate, amountPerTurn);
            resources.add(resource);
        }
    }

    public void setTerrainAt(CubeCoordinate pos, TerrainType selectedTerrain) {
        grid.getByCubeCoordinate(pos).get().getSatelliteData().get().setTerrain(selectedTerrain);
    }

    public TerrainType getTerrainAt(CubeCoordinate cube) {
        Hexagon<HexagonTile> hex = grid.getByCubeCoordinate(cube).get();
        if (hex == null) {
            return null;
        }

        HexagonTile ht = hex.getSatelliteData().get();
        if (ht == null) {
            return null;
        }

        return ht.getTerrain();
    }

	public GameSettings getSettings() {
		return settings;
	}
}
