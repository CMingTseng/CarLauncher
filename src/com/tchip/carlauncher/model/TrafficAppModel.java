package com.tchip.carlauncher.model;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/8
 * * * * * * * * * * * * * * * * * * * * * * *
 */

/**
 * 每一个App的UID和流量�??
 */
public class TrafficAppModel {
        private int uid;
        private String pkgName;
        private String appName;
        private long traffic;

        public String getAppName() {
                return appName;
        }

        public void setAppName(String appName) {
                this.appName = appName;
        }

        public long getTraffic() {
                return traffic;
        }

        public void setTraffic(long traffic) {
                this.traffic = traffic;
        }

        public int getUid() {
                return uid;
        }

        public void setUid(int uid) {
                this.uid = uid;
        }

        public String getPkgName() {
                return pkgName;
        }

        public void setPkgName(String pkgName) {
                this.pkgName = pkgName;
        }
}
