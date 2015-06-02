package com.tchip.carlauncher.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/7
 * * * * * * * * * * * * * * * * * * * * * * *
 */

@Table(name = "boot")
public class TrafficBootModel {
        @Id(column = "id")
        private int id;
        @Column(column = "time")
        private long time;

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
}
