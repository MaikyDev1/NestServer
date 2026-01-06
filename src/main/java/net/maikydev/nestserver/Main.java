package net.maikydev.nestserver;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        NestServer.SERVER.onStart();
    }
}

