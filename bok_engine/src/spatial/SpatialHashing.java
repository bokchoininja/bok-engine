package spatial;

import java.util.*;

/**
 * A generic spatial hashing system for efficient nearest neighbor detection
 * @param <T> The type of object being stored in the spatial hash
 */
public class SpatialHashing<T> {
    private final float cellSize;
    private final Map<Long, List<SpatialObject<T>>> grid;
    // Added to track object positions for efficient updates
    private final Map<T, SpatialObject<T>> objectPositions;
    
    public static class SpatialObject<T> {
        public final T object;
        public float x;
        public float y;
        public float z;
        
        public SpatialObject(T object, float x, float y, float z) {
            this.object = object;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    public SpatialHashing(float cellSize) {
        this.cellSize = cellSize;
        this.grid = new HashMap<>();
        this.objectPositions = new HashMap<>();
    }
    
    /**
     * Converts a position to a cell coordinate
     */
    private int toGridCoordinate(float position) {
        return (int) Math.floor(position / cellSize);
    }
    
    /**
     * Creates a unique hash for a 3D grid cell
     */
    private long hashCell(int x, int y, int z) {
        return ((long)x & 0x1FFFFF) | 
               (((long)y & 0x1FFFFF) << 21) |
               (((long)z & 0x1FFFFF) << 42);
    }
    
    /**
     * Gets the cell hash for a position
     */
    private long getPositionHash(float x, float y, float z) {
        return hashCell(
            toGridCoordinate(x),
            toGridCoordinate(y),
            toGridCoordinate(z)
        );
    }
    
    /**
     * Adds an object to the spatial hash
     */
    public void insert(T object, float x, float y, float z) {
        SpatialObject<T> spatialObject = new SpatialObject<>(object, x, y, z);
        long hash = getPositionHash(x, y, z);
        
        grid.computeIfAbsent(hash, k -> new ArrayList<>()).add(spatialObject);
        objectPositions.put(object, spatialObject);
    }
    
    /**
     * Updates the position of an object in the spatial hash
     * @return true if the object was found and updated, false otherwise
     */
    public boolean updatePosition(T object, float newX, float newY, float newZ) {
        SpatialObject<T> spatialObject = objectPositions.get(object);
        if (spatialObject == null) {
            return false;
        }
        
        // Calculate old and new cell hashes
        long oldHash = getPositionHash(spatialObject.x, spatialObject.y, spatialObject.z);
        long newHash = getPositionHash(newX, newY, newZ);
        
        // If the hash hasn't changed, just update the position
        if (oldHash == newHash) {
            spatialObject.x = newX;
            spatialObject.y = newY;
            spatialObject.z = newZ;
            return true;
        }
        
        // Remove from old cell
        List<SpatialObject<T>> oldCell = grid.get(oldHash);
        if (oldCell != null) {
            oldCell.remove(spatialObject);
            if (oldCell.isEmpty()) {
                grid.remove(oldHash);
            }
        }
        
        // Update position
        spatialObject.x = newX;
        spatialObject.y = newY;
        spatialObject.z = newZ;
        
        // Add to new cell
        grid.computeIfAbsent(newHash, k -> new ArrayList<>()).add(spatialObject);
        
        return true;
    }
    
    /**
     * Batch updates multiple object positions efficiently
     * @param updates Map of objects to their new positions as float arrays [x, y, z]
     * @return Set of objects that were successfully updated
     */
    public Set<T> batchUpdatePositions(Map<T, float[]> updates) {
        Set<T> updatedObjects = new HashSet<>();
        
        // Group updates by old and new cells to minimize map operations
        Map<Long, Map<Long, List<SpatialObject<T>>>> cellTransfers = new HashMap<>();
        
        for (Map.Entry<T, float[]> update : updates.entrySet()) {
            T object = update.getKey();
            float[] newPos = update.getValue();
            
            SpatialObject<T> spatialObject = objectPositions.get(object);
            if (spatialObject == null) continue;
            
            long oldHash = getPositionHash(spatialObject.x, spatialObject.y, spatialObject.z);
            long newHash = getPositionHash(newPos[0], newPos[1], newPos[2]);
            
            if (oldHash != newHash) {
                cellTransfers
                    .computeIfAbsent(oldHash, k -> new HashMap<>())
                    .computeIfAbsent(newHash, k -> new ArrayList<>())
                    .add(spatialObject);
            }
            
            // Update position
            spatialObject.x = newPos[0];
            spatialObject.y = newPos[1];
            spatialObject.z = newPos[2];
            updatedObjects.add(object);
        }
        
        // Process cell transfers
        for (Map.Entry<Long, Map<Long, List<SpatialObject<T>>>> oldCellEntry : cellTransfers.entrySet()) {
            long oldHash = oldCellEntry.getKey();
            List<SpatialObject<T>> oldCell = grid.get(oldHash);
            if (oldCell == null) continue;
            
            for (Map.Entry<Long, List<SpatialObject<T>>> newCellEntry : oldCellEntry.getValue().entrySet()) {
                long newHash = newCellEntry.getKey();
                List<SpatialObject<T>> objectsToMove = newCellEntry.getValue();
                
                // Remove from old cell
                oldCell.removeAll(objectsToMove);
                
                // Add to new cell
                grid.computeIfAbsent(newHash, k -> new ArrayList<>()).addAll(objectsToMove);
            }
            
            // Remove empty old cell
            if (oldCell.isEmpty()) {
                grid.remove(oldHash);
            }
        }
        
        return updatedObjects;
    }
    
    /**
     * Removes an object from the spatial hash
     */
    public void remove(T object) {
        SpatialObject<T> spatialObject = objectPositions.remove(object);
        if (spatialObject != null) {
            long hash = getPositionHash(spatialObject.x, spatialObject.y, spatialObject.z);
            List<SpatialObject<T>> cell = grid.get(hash);
            if (cell != null) {
                cell.remove(spatialObject);
                if (cell.isEmpty()) {
                    grid.remove(hash);
                }
            }
        }
    }
    
    // ... (rest of the previous methods remain the same: findNearby, findKNearest, etc.)
    
    /**
     * Clears all objects from the spatial hash
     */
    public void clear() {
        grid.clear();
        objectPositions.clear();
    }
    
    /**
     * Gets the current position of an object
     * @return float array [x, y, z] or null if object not found
     */
    public float[] getPosition(T object) {
        SpatialObject<T> spatialObject = objectPositions.get(object);
        if (spatialObject != null) {
            return new float[]{spatialObject.x, spatialObject.y, spatialObject.z};
        }
        return null;
    }
    
    
    /**
     * Finds all objects within a given radius of a point
     */
    public List<T> findNearby(float x, float y, float z, float radius) {
        List<T> result = new ArrayList<>();
        float radiusSquared = radius * radius;
        
        // Calculate the grid cells that need to be checked
        int minX = toGridCoordinate(x - radius);
        int maxX = toGridCoordinate(x + radius);
        int minY = toGridCoordinate(y - radius);
        int maxY = toGridCoordinate(y + radius);
        int minZ = toGridCoordinate(z - radius);
        int maxZ = toGridCoordinate(z + radius);
        
        // Check each potential grid cell
        for (int gridX = minX; gridX <= maxX; gridX++) {
            for (int gridY = minY; gridY <= maxY; gridY++) {
                for (int gridZ = minZ; gridZ <= maxZ; gridZ++) {
                    long hash = hashCell(gridX, gridY, gridZ);
                    List<SpatialObject<T>> cell = grid.get(hash);
                    
                    if (cell != null) {
                        for (SpatialObject<T> obj : cell) {
                            // Calculate actual distance
                            float dx = obj.x - x;
                            float dy = obj.y - y;
                            float dz = obj.z - z;
                            float distSquared = dx * dx + dy * dy + dz * dz;
                            
                            if (distSquared <= radiusSquared) {
                                result.add(obj.object);
                            }
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Gets the k nearest neighbors to a point
     */
    public List<T> findKNearest(float x, float y, float z, int k) {
        if (k <= 0) return new ArrayList<>();
        
        // Use a priority queue to keep track of the k nearest objects
        PriorityQueue<Map.Entry<T, Float>> nearest = new PriorityQueue<>(
            (a, b) -> b.getValue().compareTo(a.getValue())
        );
        
        // Start with a small radius and expand until we find k objects
        float radius = cellSize;
        while (nearest.size() < k) {
            List<T> nearby = findNearby(x, y, z, radius);
            
            for (T obj : nearby) {
                SpatialObject<T> so = null;
                // Find the spatial object for distance calculation
                for (List<SpatialObject<T>> cell : grid.values()) {
                    for (SpatialObject<T> spatialObj : cell) {
                        if (spatialObj.object.equals(obj)) {
                            so = spatialObj;
                            break;
                        }
                    }
                    if (so != null) break;
                }
                
                if (so != null) {
                    float dx = so.x - x;
                    float dy = so.y - y;
                    float dz = so.z - z;
                    float distSquared = dx * dx + dy * dy + dz * dz;
                    
                    nearest.offer(new AbstractMap.SimpleEntry<>(obj, distSquared));
                    if (nearest.size() > k) {
                        nearest.poll();
                    }
                }
            }
            
            radius *= 2;  // Double the search radius
            
            // Break if we've searched a very large area
            if (radius > 1000 * cellSize) break;
        }
        
        // Convert to list and reverse to get ascending order
        List<T> result = new ArrayList<>();
        while (!nearest.isEmpty()) {
            result.add(0, nearest.poll().getKey());
        }
        
        return result;
    }
}