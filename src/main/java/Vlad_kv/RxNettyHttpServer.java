package Vlad_kv;

import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;
import java.util.*;

public class RxNettyHttpServer {
    public static String buildPage() {
        return buildPage("");
    }

    public static Map<String,  Double> getCurrencyRate() {
        TreeMap<String, Double> currencyRate = new TreeMap<>();
        currencyRate.put("USD", 1.0);
        currencyRate.put("RUB", 77.3);
        currencyRate.put("EUR", 0.84);
        return currencyRate;
    }

    public static String buildPage(String table) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE HTML>")
                .append("<html>")
                .append("<head>")
                .append(  "<meta charset=\"utf-8\">")
                .append(  "<title>Каталог товаров</title>")
                .append( "</head>")
                .append( "<body>")
                .append( "<form name=\"new_user\" method=\"get\" action=\"#\">")
                .append( "<p><b>Id нового пользователя: </b>")
                .append(  "<input type=\"text\" name=\"new_user_id\" size=\"10\">")
                .append( "</p>")
                .append( "<p><b>Валюта, в которой будут отображаться товары: </b><Br>")
                .append(  "<input type=\"radio\" name=\"currency\" value=\"USD\" selected> Доллары <Br>")
                .append(  "<input type=\"radio\" name=\"currency\" value=\"RUB\"> Рубли <Br>")
                .append(  "<input type=\"radio\" name=\"currency\" value=\"EUR\"> Евро <Br>")
                .append( "</p>")
                .append( "<input type=\"submit\" value=\"Добавить пользователя\">")
                .append( "</form>")
                .append( "<form>")
                .append( "<p><b>Имя нового товара: </b>")
                .append(  "<input type=\"text\" name=\"new_product_name\" size=\"10\">")
                .append( "<p><b>Цена нового товара: </b>")
                .append(  "<input type=\"text\" name=\"product_price\" size=\"10\">")
                .append( "<input type=\"submit\" value=\"Добавить товар\">")
                .append( "</form>")
                .append( "<form name=\"new_user\" method=\"get\" action=\"#\">")
                .append(  "<button type=\"submit\" name=\"Show_table\" value=\"Show_users\">Показать всех пользователей</button>")
                .append(  "<button type=\"submit\" name=\"Show_table\" value=\"Show_products\">Показать список товаров для пользователя</button>")
                .append("<input type=\"text\" name=\"user_id\" size=\"10\">")
                .append( "</form>")
                .append(table)
                .append( "</body>")
                .append("</html>");
        return sb.toString();
    }

    private static String createTable(String title1, String title2, Collection<Map.Entry<String, String>> rows) {
        StringBuilder sb = new StringBuilder();

        sb.append("<table><tr>");
        sb.append("<th>" + title1 + "</th>");
        sb.append("<th>" + title2 + "</th>");
        sb.append("</tr>");

        for (Map.Entry<String, String> row : rows) {
            sb.append("<tr><td>")
                    .append(row.getKey())
                    .append("</td><td>")
                    .append(row.getValue())
                    .append("</td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public static void main(final String[] args) {
        HttpServer
                .newServer(8080)
                .start((req, resp) -> {

                    System.out.println(req.toString());

                    Map<String, List<String>> parameters = req.getQueryParameters();

//                    for (Map.Entry<String, List<String>> k : parameters.entrySet()) {
//                        System.out.print(k.getKey() + " : ");
//                        for (String val : k.getValue()) {
//                            System.out.print(val + " ");
//                        }
//                        System.out.println();
//                    }

                    Observable<String> response;

                    if (parameters.containsKey("new_user_id")) {
                        int id = Integer.parseInt(parameters.get("new_user_id").get(0));
                        String currency = "USD";
                        if (parameters.containsKey("currency")) {
                            currency = parameters.get("currency").get(0);
                        }

                        response = ReactiveMongoDriver.addUser(id, currency).map(s -> buildPage());
                    } else if (parameters.containsKey("Show_table")) {
                        if (parameters.get("Show_table").get(0).equals("Show_users")) {
                            response = ReactiveMongoDriver.getUsers().map(m -> {
                                return buildPage(createTable("Id пользователя", "Предпочитаемая валюта", m.entrySet()));
                            });
                        } else {
                            Observable<String> o = Observable.just("USD");
                            if (parameters.containsKey("user_id")) {
                                o = ReactiveMongoDriver.getUser(Integer.parseInt(parameters.get("user_id").get(0)))
                                        .map(u -> u.currency);
                            }

                            response = o.withLatestFrom(ReactiveMongoDriver.getProducts(1.0), (cur, m) -> {
                                m.replaceAll((k, v) -> String.valueOf(Double.parseDouble(v) * getCurrencyRate().getOrDefault(cur, 1.0)));
                                return m;
                            }).map(m -> buildPage(createTable("Название товара", "Стоимость товарa", m.entrySet())  ));
                        }
                    } else if (parameters.containsKey("new_product_name")) {
                        String name = parameters.get("new_product_name").get(0);
                        Double price = Double.valueOf(parameters.get("product_price").get(0));
                        response = ReactiveMongoDriver.addProduct(name, price).map(s -> buildPage());
                    } else {
                        response = Observable.just(buildPage());
                    }
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }
}
