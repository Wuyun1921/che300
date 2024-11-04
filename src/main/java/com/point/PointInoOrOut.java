package com.point;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/*
   交点奇偶数判定
*  功能：判断点是否在多边形内
   方法：求解通过该点的水平线与多边形各边的交点
   结论：单边交点为奇数，成立!单边交点为偶数，不成立!

   taps：如果要执行，记得更换latlon.json文件的地址！！！
*
* */
public class PointInoOrOut {

    public static void main(String[] args) {
        // 定义四个点
        double[][] points = {
                {119.037089, 32.259867}, // p1
                {118.821489, 32.077388}, // p2
                {118.80657099, 32.0353893}, // p3
                {118.894173, 32.077862} // p4
        };

        // 从 latlon.json 文件中读取多边形顶点
        double[][] polygon = readPolygonFromJson("/opt/points__inorout/latlon.json");

        // 判断每个点是否在多边形内
        for (int i = 0; i < points.length; i++) {
            boolean isInside = isPointInPolygon(points[i], polygon);
            System.out.println("点 p" + (i + 1) + " " + (isInside ? "在" : "不在") + " 多边形内。");
            analyzePointInPolygon(points[i], polygon);
        }

        // 输出总结
        summarizeResults(points, polygon);
    }

    private static double[][] readPolygonFromJson(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONArray jsonArray = new JSONArray(content);
            double[][] polygon = new double[jsonArray.length()][2];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray point = jsonArray.getJSONArray(i);
                polygon[i][0] = point.getDouble(0); // 经度
                polygon[i][1] = point.getDouble(1); // 纬度
            }
            return polygon;
        } catch (IOException e) {
            e.printStackTrace();
            return new double[0][0];
        }
    }

    private static boolean isPointInPolygon(double[] point, double[][] polygon) {
        int n = polygon.length;
        boolean inside = false;
        double x = point[0], y = point[1];

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon[i][0], yi = polygon[i][1];
            double xj = polygon[j][0], yj = polygon[j][1];

            boolean intersect = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }

    private static void summarizeResults(double[][] points, double[][] polygon) {
        System.out.println("\n总结结果：");
        for (int i = 0; i < points.length; i++) {
            boolean isInside = isPointInPolygon(points[i], polygon);
            System.out.println("点 p" + (i + 1) + " 的坐标为 (" + points[i][0] + ", " + points[i][1] + ")，" + (isInside ? "在" : "不在") + " 多边形内。");
        }
    }

    private static void analyzePointInPolygon(double[] point, double[][] polygon) {
        System.out.println("分析点 (" + point[0] + ", " + point[1] + ") 在多边形内的判断过程：");

        int n = polygon.length;
        int intersectionCount = 0; // 交点计数
        boolean isInside = false; // 是否在多边形内
        double x = point[0], y = point[1];

        // 遍历多边形的每条边
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon[i][0], yi = polygon[i][1];
            double xj = polygon[j][0], yj = polygon[j][1];

            // 判断当前边是否与射线相交
            boolean intersect = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) {
                intersectionCount++;
                isInside = !isInside; // 每次交点改变inside状态
                System.out.println("与边 (" + xi + ", " + yi + ") 到 (" + xj + ", " + yj + ") 的交点相交，当前交点数：" + intersectionCount);
            }
        }

        // 输出分析结果
        System.out.println("点 (" + point[0] + ", " + point[1] + ") 最终判断为 " + (isInside ? "在" : "不在") + " 多边形内。");
    }
}
