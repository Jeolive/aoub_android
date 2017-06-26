package com.olive.aoub_android.sdk.model;

import java.util.List;

/**
 * 上传数据对象bean
 * Created by jeyOlive on 2017-06-5.
 */
public class DataBlockList {
    private List<DataBlockInfo> data_block_list;

    public List<DataBlockInfo> getData_block_list() {
        return data_block_list;
    }

    public void setData_block_list(List<DataBlockInfo> data_block_list) {
        this.data_block_list = data_block_list;
    }
}
