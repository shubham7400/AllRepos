package com.blueduck.annotator.encryption

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource

@UnstableApi
class EncryptedFileDataSourceFactory(var dataSource: DataSource, val password: String) : DataSource.Factory {

    override fun createDataSource(): DataSource {
        return EncryptedDataSource(dataSource, password)
    }
}
