package cn.linuxcrypt.common.page;


import cn.linuxcrypt.common.Constant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 实现分页辅助类
 * @author clibing
 */
@Data
public class Page extends Pagination {
    /** 根据时间偏移 */
    private Long afterTime;
    private Double afterScore;
    /** 查询数据列表 */
    private List records = Collections.emptyList();

    protected Page() {
        /* 保护 */
    }

    public Page(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            pageNumber = Constant.Page.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null) {
            pageSize = Constant.Page.DEFAULT_PAGE_SIZE;
        }

        if (pageNumber < Constant.Page.DEFAULT_PAGE_NUMBER) {
            pageNumber = Constant.Page.DEFAULT_PAGE_NUMBER;
        }

        super.setCurrent(pageNumber);
        super.setSize(pageSize);
    }

    @Override
    public String toString() {
        StringBuffer pg = new StringBuffer();
        pg.append(" Page:{ [").append(super.toString()).append("], ");
        if (records != null) {
            pg.append("records-size:").append(records.size());
        } else {
            pg.append("records is null");
        }
        return pg.append(" }").toString();
    }

    public static Page getInstance(Integer pageNumber, Integer pageSize) {
        return new Page(pageNumber, pageSize);
    }

    public static <T> Page getInstance(Long total, List<T> list, Integer pageNumber, Integer pageSize) {
        Page page = new Page(pageNumber, pageSize);
        page.setTotal(total);
        page.setRecords(list);
        return page;
    }

    public static Page getInstanceTime(Long afterTime) {
        Page p = new Page();
        p.setAfterTime(afterTime);
        return p;
    }

    public static Page getInstanceScore(Double afterScore) {
        Page p = new Page();
        p.setAfterScore(afterScore);
        return p;
    }

    @JsonIgnore
    public boolean isFirst() {
        return getCurrent() == Constant.Number.I.ONE;
    }

    @JsonIgnore
    public boolean isLast() {
        return getCurrent() == getTotal();
    }

}
