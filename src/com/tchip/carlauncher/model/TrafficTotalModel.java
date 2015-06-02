package com.tchip.carlauncher.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/6
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * 记录累计获得多少流量（临时保存用，重启手机会清空�?
 */
@Table(name = "traffic_total")
public class TrafficTotalModel {
        @Id(column = "id")
        private int id;
        @Column(column = "type")
        private int type;
        @Column(column = "traffic")
        private long traffic;
        @Column(column = "time")
        private long time;

        public long getTraffic() {
                return traffic;
        }

        public void setTraffic(long traffic) {
                this.traffic = traffic;
        }

        public int getId() {

                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public long getTime() {
                return time;
        }

        public void setTime(long time) {
                this.time = time;
        }

        public int getType() {
                return type;
        }

        public void setType(int type) {
                this.type = type;
        }
}
