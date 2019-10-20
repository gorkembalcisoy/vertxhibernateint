package com.gbalcisoy.application;

import com.gbalcisoy.model.DummyTable;
import com.gbalcisoy.model.DummyTable2;
import com.gbalcisoy.service.DummyTable2Service;
import com.gbalcisoy.service.DummyTable2ServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;

public class Server extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        super.start();
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(req -> {
            if (req.method() == HttpMethod.GET) {
                req.response().setChunked(true);
                DummyTable dummyTable = new DummyTable();
                dummyTable.setName("dummyTable");
                DummyTable2 dummyTable2 = new DummyTable2();
                dummyTable2.setName("dummyTable2");
                dummyTable2.setDummyTable(dummyTable);
                DummyTable2Service dummyTable2Service = new DummyTable2ServiceImpl();
                dummyTable2Service.saveOrUpdate(dummyTable2, result -> {
                    System.out.println("HIBERNATE DENEME DENEME DENEME DENEME DENEME!!!!!!");
                    System.out.println("RESULT: " + result.toString());
                    req.response().setStatusCode(200).write(result.toString());
                });
            }
        });

        server.listen(8080);
    }
}
