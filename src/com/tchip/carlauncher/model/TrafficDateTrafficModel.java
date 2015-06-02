package com.tchip.carlauncher.model;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/10
 * * * * * * * * * * * * * * * * * * * * * * *
 */

/**
 * 时间与流量的model
 */
public class TrafficDateTrafficModel {
        private long date;
        private long traffic;

        public long getDate() {
                return date;
        }

        public void setDate(long date) {
                this.date = date;
        }

        public long getTraffic() {
                return traffic;
        }

        public void setTraffic(long traffic) {
                this.traffic = traffic;
        }
}
