package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HashMap<String, String> params = (HashMap<String, String>) getParams(args);
        boolean paramsCorrectness = checkParamsCorrectness(params);
        System.out.println("paramsCorrectness: " + paramsCorrectness);
        System.out.println("params: " + params);

        if(paramsCorrectness) {
            String workingMode = params.get("mode");
            int port = Integer.parseInt(params.get("port"));
            if (workingMode.equals("server")) {
                System.out.println("About to start server...");
                GreetServer server = new GreetServer(params.get("map"));
                server.start(port);
            } else {
                System.out.println("About to start client...");
                GreetClient client = new GreetClient(params.get("map"));
                client.startConnection(params.get("address"), port);
            }
        }

    }

    private static Map<String, String> getParams(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        String lastParam = null;
        for(var arg : args) {
            if(isParameter(arg)) lastParam = arg.substring(1);
            else params.put(lastParam, arg);
        }
        return params;
    }

    private static boolean isParameter(String text) {
        return text.charAt(0) == '-';
    }

    private static boolean checkModeParamCorrectness(Map<String, String> params) {
        if (params.containsKey("mode")) {
            if(!params.get("mode").matches("server|client")) {
                System.err.println("Mode should be 'server' or 'client'.");
                return false;
            }
        } else return false;
        return true;
    }

    private static boolean checkPortParamCorrectness(Map<String, String> params) {
        if (params.containsKey("port")) {
            int port = -1;
            try {
                port = Integer.parseInt(params.get("port"));
            } catch (NumberFormatException e) {
                System.err.println("Port number is not a number.");
                e.printStackTrace();
            }

            if(!(port >= 0 && port <= 65535)) {
                System.err.println("Port number is not correct.");
                return false;
            }
        }

        return true;
    }

    private static boolean checkAddressParamCorrectness(Map<String, String> params) {
        if(params.containsKey("address")) {
            if(!(params.get("mode").equals("client"))) {
                System.err.println("Address parameter should be only added in client mode");
                return false;
            }
            else {
                if(!(params.get("address").matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"))) {
                    System.err.println("Wrong IP format.");
                    return false;
                }
            }
        } else {
            if(params.get("mode").equals("client")) {
                System.err.println("No -address parameter given for client mode.");
                return false;
            }
        }
        return true;
    }

    private static boolean checkMapParamCorrectness(Map<String, String> params) {
        if(!params.containsKey("map")) {
            System.err.println("No map parameter provided.");
            return false;
        }
        return true;
    }


    private static boolean checkParamsCorrectness(Map<String, String> params) {
        if(!checkModeParamCorrectness(params)) return false;
        if(!checkPortParamCorrectness(params)) return false;
      //  if(!checkAddressParamCorrectness(params)) return false;
        if(!checkMapParamCorrectness(params)) return false;
        return true;
    }


}
