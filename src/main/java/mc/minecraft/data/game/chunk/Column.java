package mc.minecraft.data.game.chunk;


public class Column {
    private int x;
    private int z;
    private Chunk chunks[];
    private byte biomeData[];
    private Object tileEntities[];

    private boolean skylight;

    public Column(int x, int z, Chunk chunks[], Object[] tileEntities) {
        this(x, z, chunks, null, tileEntities);
    }

    public Column(int x, int z, Chunk chunks[], byte biomeData[], Object[] tileEntities) {
        if (chunks.length != 16) {
            throw new IllegalArgumentException("Chunk array length must be 16.");
        }

        if (biomeData != null && biomeData.length != 256) {
            throw new IllegalArgumentException("Biome data array length must be 256.");
        }

        this.skylight = false;
        boolean noSkylight = false;
        for (Chunk chunk : chunks) {
            if (chunk != null) {
                if (chunk.getSkyLight() == null) {
                    noSkylight = true;
                } else {
                    this.skylight = true;
                }
            }
        }

        if (noSkylight && this.skylight) {
            throw new IllegalArgumentException("Either all chunks must have skylight values or none must have them.");
        }

        this.x = x;
        this.z = z;
        this.chunks = chunks;
        this.biomeData = biomeData;
        this.tileEntities = tileEntities != null ? tileEntities : new Object[0];
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public Chunk[] getChunks() {
        return this.chunks;
    }

    public boolean hasBiomeData() {
        return this.biomeData != null;
    }

    public byte[] getBiomeData() {
        return this.biomeData;
    }

    public Object[] getTileEntities() {
        return this.tileEntities;
    }

    public boolean hasSkylight() {
        return this.skylight;
    }
}
