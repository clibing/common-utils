package cn.linuxcrypt.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @author clibing
 */
@Data
public class Page<T> {
    private List<T> list;

    private Long offset;
    private Long limit;
    private Long total;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer pages;
    @JsonIgnore
    private transient Boolean counted = Boolean.TRUE;

    public Page() {
        offset = Constant.Page.DEFAULT_OFFSET;
        limit = Constant.Page.DEFAULT_LIMIT;
    }

    public Page(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        if (pageNumber == null) {
            this.pageNumber = Constant.Page.DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null) {
            this.pageNumber = Constant.Page.DEFAULT_PAGE_SIZE;
        }
    }

    public Page(Integer pageNumber, Integer pageSize, Long total) {
        this(pageNumber, pageSize);
        if (total != null) {
            this.total(total);
        }
    }

    public void total(Long total) {
        this.total = total;
        if (total == null) {
            pages = 0;
            return;
        }
        if (pageSize > 0) {
            pages = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
        } else {
            pages = 0;
        }
    }

    public Page(Long offset, Long limit) {
        this.offset = offset;
        this.limit = limit;
        if (offset == null) {
            this.offset = Constant.Page.DEFAULT_OFFSET;
        }
        if (limit == null) {
            this.limit = Constant.Page.DEFAULT_LIMIT;
        }
        if (limit > Constant.Page.DEFAULT_MAX_LIMIT) {
            this.limit = Constant.Page.DEFAULT_MAX_LIMIT;
        }
    }


}
