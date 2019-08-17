package cn.linuxcrypt.common.entity;

import java.io.Serializable;

/**
 * @author clibing
 */
public interface SuperIdEntity<ID> extends Serializable {
    /**
     * 系统关键词
     *
     * @return
     */
    ID getId();
}