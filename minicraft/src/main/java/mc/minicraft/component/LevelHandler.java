package mc.minicraft.component;

import mc.minicraft.component.entity.Entity;

import java.util.Objects;

public interface LevelHandler {

    void reset();

    void setData(int x, int y, int data);

    void setTile(int x, int y, int tile);

    void insertEntity(int x, int y, Entity entity);

    void removeEntity(int x, int y, Entity entity);

    final class DataKey {
        public final int x;
        public final int y;
        public final int value;

        public DataKey(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataKey dataKey = (DataKey) o;
            return x == dataKey.x &&
                    y == dataKey.y &&
                    value == dataKey.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, value);
        }

        @Override
        public String toString() {
            return "DataKey{" +
                    "x=" + x +
                    ", y=" + y +
                    ", value=" + value +
                    '}';
        }
    }
}
