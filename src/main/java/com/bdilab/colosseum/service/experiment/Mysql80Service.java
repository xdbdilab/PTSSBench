package com.bdilab.colosseum.service.experiment;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/21 15:10
 */
@Deprecated
public interface Mysql80Service {
//    public String execSysbenchMysql80(String innodb_buffer_pool_size, String innodb_thread_concurrency, String innodb_adaptive_hash_index, String innodb_flush_log_at_trx_commit,
//                                      String join_buffer_size, String sort_buffer_size, String tmp_table_size, String thread_cache_size, String read_rnd_buffer_size,
//                                      String max_heap_table_size);
public String execSysbenchMysql80(String softwareLocationBOStr, String paramList, String performance);
}
