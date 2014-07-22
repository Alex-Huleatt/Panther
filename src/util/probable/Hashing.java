/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.probable;

/**
 *
 * @author Alex
 */
class Hashing {

    public static long fnv64(final byte[] data, int length, long seed) {
        for (int i = 0; i < length; i++) {
            seed ^= data[i];
            seed += (seed << 1) + (seed << 4) + (seed << 5)
                    + (seed << 7) + (seed << 8) + (seed << 40);
        }
        return seed;
    }

    public static long murmur64(final byte[] data, int length, int seed) {
        final long m = 0xc6a4a7935bd1e995L;
        final int r = 47;

        long h = (seed & 0xffffffffl) ^ (length * m);

        int length8 = length / 8;

        for (int i = 0; i < length8; i++) {
            final int i8 = i * 8;
            long k = ((long) data[i8 + 0] & 0xff) + (((long) data[i8 + 1] & 0xff) << 8)
                    + (((long) data[i8 + 2] & 0xff) << 16) + (((long) data[i8 + 3] & 0xff) << 24)
                    + (((long) data[i8 + 4] & 0xff) << 32) + (((long) data[i8 + 5] & 0xff) << 40)
                    + (((long) data[i8 + 6] & 0xff) << 48) + (((long) data[i8 + 7] & 0xff) << 56);

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        switch (length % 8) {
            case 7:
                h ^= (long) (data[(length & ~7) + 6] & 0xff) << 48;
            case 6:
                h ^= (long) (data[(length & ~7) + 5] & 0xff) << 40;
            case 5:
                h ^= (long) (data[(length & ~7) + 4] & 0xff) << 32;
            case 4:
                h ^= (long) (data[(length & ~7) + 3] & 0xff) << 24;
            case 3:
                h ^= (long) (data[(length & ~7) + 2] & 0xff) << 16;
            case 2:
                h ^= (long) (data[(length & ~7) + 1] & 0xff) << 8;
            case 1:
                h ^= (long) (data[length & ~7] & 0xff);
                h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        return h;
    }

    public static long getHash(byte[] data, int n, int seed) {
        int len = data.length;
        return murmur64(data, len, seed) + n * fnv64(data, len, seed);
    }

    /**
     * Simple hash function that produces a hash for 2 shorts and a byte.
     * @param x duck
     * @param y duck
     * @param n goose
     * @return The hash.
     */
    public static int getPointHash(short x, short y, byte n) {
        n = (byte) (67 - n); //67 seems like a good number;
        short a = 0; //first hash function, concat the two values
        a |= x; //shove the x in
        a <<= 8; //shift that shit over
        a |= y; //shove that y up there
        short b = (short) (((x + y) * (x + y + 1) >> 1) + y + 1); //cantor pairing function. + 1
        return (a + b * n); //combine the two hashes
    }

}
