package cn.linuxcrypt.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * 实现分页辅助类
 * @author clibing
 */
public class Page<T> {
    /** 查询数据列表 */
    @Getter @Setter
    private List<T> records = Collections.emptyList();

    /** 总数 */
    @Getter @Setter
    private Long totalRow;

    /** 每页显示条数 */
    @Getter @Setter
    private Integer pageSize;

    /** 总页数 */
    @Getter @Setter
    private Integer totalPage;

    /** 当前页 */
    @Getter @Setter
    private Integer pageNumber;

    @Getter @Setter
    private Long offset;

    @Getter @Setter
    private Long limit;

    /** 根据时间偏移 */
    @Getter @Setter
    private Long afterTime;

    @Getter @Setter
    private Double afterScore;

    public Page(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            this.pageNumber = Constant.Page.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null) {
            this.pageSize = Constant.Page.DEFAULT_PAGE_SIZE;
        }

        if (pageNumber < Constant.Page.DEFAULT_PAGE_NUMBER) {
            this.pageNumber = Constant.Page.DEFAULT_PAGE_NUMBER;
        }

        this.offset = (pageNumber - 1) * pageSize * 1L;
        this.limit = pageSize * 1L;
    }

    public Page(Integer pageSize, Long afterTime) {
        this.pageSize = pageSize;
        this.afterTime = afterTime;
        this.offset = Constant.Number.L.ZERO;
        this.limit = pageSize * 1L;
    }

    public Page(Integer pageSize, Double afterScore) {
        this.pageSize = pageSize;
        this.afterScore = afterScore;
        this.offset = Constant.Number.L.ZERO;
        this.limit = pageSize * 1L;
    }

    public Long getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(long totalRow) {
        this.totalRow = totalRow;
        this.totalPage = Math.toIntExact((this.totalRow / this.pageSize));
        if (this.totalRow % this.pageSize != 0) {
            this.totalPage++;
        }
    }

    public static Page getInstance(Integer pageNumber, Integer pageSize) {
        return new Page(pageNumber, pageSize);
    }

    public static <T> Page getInstance(Long total, List<T> list, Integer pageNumber, Integer pageSize) {
        Page page = new Page(pageNumber, pageSize);
        page.setTotalRow(total);
        page.setRecords(list);
        return page;
    }

    public static Page getInstanceTime(Long afterTime, Integer pageSize) {
        Page p = new Page(pageSize, afterTime);
        p.setAfterTime(afterTime);
        return p;
    }

    public static Page getInstanceScore(Double afterScore, Integer pageSize) {
        Page p = new Page(pageSize, afterScore);
        p.setAfterScore(afterScore);
        return p;
    }

    @JsonIgnore
    public boolean isFirst() {
        return getPageNumber() == Constant.Number.I.ONE;
    }

    @JsonIgnore
    public boolean isLast() {
        return getTotalPage().equals(getPageNumber());
    }

}
