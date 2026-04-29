package net.maikydev.nestserver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchMethodException {
        NestServer.SERVER.onStart();
//        DuckletController controller = DuckletController.createController(8080, 10);
//        controller.addNewMapping(new ExampleRoute());
//        controller.startController();
    }
}

