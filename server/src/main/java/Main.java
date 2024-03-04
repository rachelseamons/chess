import chess.*;
import dataAccess.DataAccessMemory;
import server.Server;
import service.ChessService;

public class Main {
    public static void main(String[] args) {
        var dataAccess = new DataAccessMemory();
        var service = new ChessService(dataAccess);
        var server = new Server(service);
        server.run(8080);
    }
}