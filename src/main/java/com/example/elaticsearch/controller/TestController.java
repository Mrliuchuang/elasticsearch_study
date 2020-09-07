package com.example.elaticsearch.controller;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Controller
public class TestController {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @RequestMapping("/get")
    @ResponseBody
    public  String searchEs() throws IOException {
        SearchRequest searchRequest = new SearchRequest("movie");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("title", "Spectre"));
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        List<Integer> shopIdList = new ArrayList<>();
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit:hits) {
            System.out.println("tagline:"+hit.getSourceAsMap().get("tagline").toString());
        }
        searchRequest.source(sourceBuilder);
        return shopIdList.toString();
    }
    @RequestMapping("/getlow")
    @ResponseBody
    public  String searchEsLow() throws IOException {
        Request request = new Request("GET", "movie/_search");
        String reqJsom = "{\n" +
                "  \"query\":{\n" +
                "    \"bool\":{\n" +
                "      \"filter\":[\n" +
                "        {\"term\":{\"title\":\"steve\"}},\n" +
                "        {\"term\":{\"cast.name\":\"gaspard\"}},\n" +
                "        {\"range\": { \"release_date\": { \"lte\": \"2015/01/01\" }}},\n" +
                "        {\"range\": { \"popularity\": { \"gte\": \"25\" }}}\n" +
                "        ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\":[\n" +
                "    {\"popularity\":{\"order\":\"desc\"}}\n" +
                "  ]\n" +
                "}";
        System.out.println(reqJsom);
        request.setJsonEntity(reqJsom);
        Response response =restHighLevelClient.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        return responseStr;
    }
}
