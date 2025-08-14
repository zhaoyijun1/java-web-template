package com.zyj.demo.tree;


import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @author zyj
 * @version 1.0
 */
public class ZyjTest {

    public List<Map<String, Object>> sortTree(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 确定最小编码长度
        int minLen = list.stream()
                .mapToInt(node -> ((String) node.get("view_code")).length())
                .min()
                .orElse(0);

        // 2. 构建所有节点编码的集合
        Set<String> allCodes = new HashSet<>();
        for (Map<String, Object> node : list) {
            allCodes.add((String) node.get("view_code"));
        }

        // 3. 构建父节点到子节点的映射
        Map<String, List<Map<String, Object>>> childrenMap = new HashMap<>();
        for (Map<String, Object> node : list) {
            String code = (String) node.get("view_code");
            if (code.length() > minLen) {
                String parentCode = code.substring(0, code.length() - 3);
                childrenMap.computeIfAbsent(parentCode, k -> new ArrayList<>()).add(node);
            }
        }

        // 4. 识别根节点
        List<Map<String, Object>> rootList = new ArrayList<>();
        for (Map<String, Object> node : list) {
            String code = (String) node.get("view_code");
            if (code.length() == minLen) {
                rootList.add(node); // 最小长度节点
            } else {
                String parentCode = code.substring(0, code.length() - 3);
                if (!allCodes.contains(parentCode)) {
                    rootList.add(node); // 父节点不存在的节点
                }
            }
        }

        // 5. 对根节点排序：优先按serial_index，再按view_code
        rootList.sort((node1, node2) -> {
            int cmp = Integer.compare(
                    getSerialIndex(node1),
                    getSerialIndex(node2)
            );
            return cmp != 0 ? cmp :
                    ((String) node1.get("view_code")).compareTo((String) node2.get("view_code"));
        });

        // 6. DFS遍历生成排序结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> root : rootList) {
            dfs(root, childrenMap, result);
        }

        return result;
    }

    private void dfs(Map<String, Object> node,
                     Map<String, List<Map<String, Object>>> childrenMap,
                     List<Map<String, Object>> result) {
        result.add(node); // 添加当前节点
        String code = (String) node.get("view_code");
        List<Map<String, Object>> children = childrenMap.get(code);
        if (children != null) {
            // 子节点排序：优先按serial_index，再按view_code
            children.sort((child1, child2) -> {
                int cmp = Integer.compare(
                        getSerialIndex(child1),
                        getSerialIndex(child2)
                );
                return cmp != 0 ? cmp :
                        ((String) child1.get("view_code")).compareTo((String) child2.get("view_code"));
            });

            for (Map<String, Object> child : children) {
                dfs(child, childrenMap, result); // 递归子节点
            }
        }
    }

    // 安全获取serial_index（处理null/类型转换）
    private int getSerialIndex(Map<String, Object> node) {
        Object index = node.get("serial_index");
        if (index == null) return 0; // 默认值
        if (index instanceof Number) return ((Number) index).intValue();
        if (index instanceof String) {
            try {
                return Integer.parseInt((String) index);
            } catch (NumberFormatException e) {
                return 0; // 解析失败返回默认值
            }
        }
        return 0; // 其他类型默认值
    }

    // 辅助方法：添加测试节点
    private void addNode(List<Map<String, Object>> list, String viewCode, int serialIndex) {
        Map<String, Object> node = new HashMap<>();
        node.put("view_code", viewCode);
        node.put("serial_index", serialIndex);
        list.add(node);
    }

    @Test
    public void test() {
        // 示例数据
        List<Map<String, Object>> data = new ArrayList<>();
//        addNode(data, "001", 0);
//        addNode(data, "001001", 2);
        addNode(data, "001002", 1);
//        addNode(data, "001003", 3);
        addNode(data, "001003001", 0);
//        addNode(data, "001004", 6);
//        addNode(data, "001004001", 0);
//        addNode(data, "001005", 5);

        // 排序处理
        List<Map<String, Object>> sorted = sortTree(data);

        // 打印结果
        for (Map<String, Object> node : sorted) {
            System.out.printf("view_code: %s, serial_index: %d%n",
                    node.get("view_code"),
                    getSerialIndex(node));
        }
    }


}
