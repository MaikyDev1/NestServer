package net.maikydev.nestserver;

import java.io.FileNotFoundException;

public class Main {
    static void main(String[] args) throws FileNotFoundException {
        NestServer.SERVER.onStart();
    }
}

