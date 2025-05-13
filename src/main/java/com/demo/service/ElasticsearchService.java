package com.demo.service;

import com.alibaba.fastjson2.JSONObject;
import com.demo.elasticseatch.DiscussPostRepository;
import com.demo.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    public List<Object> searchDiscussPost(String keyword, int current, int limit) throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");
        //显示查询关键字的形态
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .preTags("<em>")
                .postTags("</em>")
                .field("title")
                .field("content")
                .requireFieldMatch(false);
        //搜索请求配置
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .sort(SortBuilders.fieldSort("discussPostType").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.ASC))
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .from(current).size(limit).highlighter(highlightBuilder); //高亮配置传入

        // 搜索请求传入
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<DiscussPost> list = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            list.add(discussPost);
        }
        List<Object> res = new ArrayList<>();
        res.add((int)searchResponse.getHits().getTotalHits().value);
        res.add(list);
        return res;
    }

}
