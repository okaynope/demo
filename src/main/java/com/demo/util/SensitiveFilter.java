package com.demo.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootTrieNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            logger.error("加载敏感词文件失败：", e.getMessage());
        }
    }

    // 添加敏感词
    public void addKeyword(String keyword) {
        TrieNode tempNode = rootTrieNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNodes(c);

            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNodes(c, subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if (i == keyword.length()-1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 三个指针
        TrieNode tempNode = rootTrieNode;
        int begin = 0;
        int position = 0;

        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                if (tempNode == rootTrieNode) {
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            // 检测下节点
            tempNode = tempNode.getSubNodes(c);
            if (tempNode == null) {
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootTrieNode;
            } else if (tempNode.isKeywordEnd()) {
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootTrieNode;
            } else {
                position++;
            }
        }

        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断符号
    private boolean isSymbol(Character c) {
        // 非特殊符号，非东亚符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    private class TrieNode {

        // 关键词结束
        private boolean isKeywordEnd = false;

        // 子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean KeywordEnd) {
            isKeywordEnd = KeywordEnd;
        }

        public void addSubNodes(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNodes(Character c) {
            return subNodes.get(c);
        }

    }

}
