package github.javaguide.compress;

public interface Compress {
    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
