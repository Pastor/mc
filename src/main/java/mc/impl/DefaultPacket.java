package mc.impl;

import mc.api.Buffer;
import mc.api.Packet;

import java.io.IOException;

public abstract class DefaultPacket implements Packet {
    public static final class Header implements Packet.Header {

        public boolean isLengthVariable() {
            return true;
        }

        public int getLengthSize() {
            return 5;
        }

        public int getLengthSize(int length) {
            if ((length & -128) == 0) {
                return 1;
            } else if ((length & -16384) == 0) {
                return 2;
            } else if ((length & -2097152) == 0) {
                return 3;
            } else if ((length & -268435456) == 0) {
                return 4;
            } else {
                return 5;
            }
        }

        public int readLength(Buffer.Input in, int available) throws IOException {
            return in.readVarInt();
        }

        public void writeLength(Buffer.Output out, int length) throws IOException {
            out.writeVarInt(length);
        }

        public int readPacketId(Buffer.Input in) throws IOException {
            return in.readVarInt();
        }

        public void writePacketId(Buffer.Output out, int packetId) throws IOException {
            out.writeVarInt(packetId);
        }
    }

}
