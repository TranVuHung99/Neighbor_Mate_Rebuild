package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Point {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("point")
    private int point;

    @SerializedName("bonus_point")
    private String bonus_point;

    @SerializedName("amount")
    private int amount;

    @SerializedName("identifier")
    private String sku;

    @SerializedName("package_img")
    private String image;

    @SerializedName("rank_package_img")
    private String rankImage;

    @SerializedName("slug")
    private String slug;


    public Point(String id, String title, int point, String bonus_point, int amount, String sku, String image, String rankImage) {
        this.id = id;
        this.title = title;
        this.point = point;
        this.bonus_point = bonus_point;
        this.amount = amount;
        this.sku = sku;
        this.image = image;
        this.rankImage = rankImage;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", point=" + point +
                ", bonus_point='" + bonus_point + '\'' +
                ", amount=" + amount +
                ", sku='" + sku + '\'' +
                ", image='" + image + '\'' +
                ", rankImage='" + rankImage + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getBonus_point() {
        return bonus_point;
    }

    public void setBonus_point(String bonus_point) {
        this.bonus_point = bonus_point;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRankImage() {
        return rankImage;
    }

    public void setRankImage(String rankImage) {
        this.rankImage = rankImage;
    }

    public static class PointResponse {
        @SerializedName("code")
        private String code;

        @SerializedName("stattus")
        private String status;

        @SerializedName("message")
        private String message;


        @SerializedName("data")
        private Data data;

        public PointResponse(String code, String status, Data data) {
            this.code = code;
            this.status = status;
            this.data = data;
        }

        @Override
        public String toString() {
            return "PointResponse{" +
                    "code='" + code + '\'' +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data {
            @SerializedName("result")
            private List<Point> pointList;

            @SerializedName("balance")
            private String totalPoint;

            public Data(List<Point> pointList, String totalPoint) {
                this.pointList = pointList;
                this.totalPoint = totalPoint;
            }

            @Override
            public String toString() {
                return "Data{" +
                        "pointList=" + pointList +
                        "totalPoint" + totalPoint +
                        '}';
            }

            public String getTotalPoint() {
                return totalPoint;
            }

            public void setTotalPoint(String totalPoint) {
                this.totalPoint = totalPoint;
            }

            public List<Point> getPointList() {
                return pointList;
            }

            public void setPointList(List<Point> pointList) {
                this.pointList = pointList;
            }
        }
    }
}
