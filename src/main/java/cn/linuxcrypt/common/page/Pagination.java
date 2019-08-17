package cn.linuxcrypt.common.page;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 简单分页模型
 * </p>
 * 用户可以通过继承 org.apache.ibatis.session.RowBounds实现自己的分页模型<br>
 * 注意：插件仅支持RowBounds及其子类作为分页参数
 */
public class Pagination {
    /**
     * 总数
     */
    private long total;

    /**
     * 每页显示条数
     */
    @Getter
    @Setter
    private int size;

    /**
     * 总页数
     */
    @Getter
    @Setter
    private int pages;

    /**
     * 当前页
     */
    @Getter
    @Setter
    private int current = 1;

    public Pagination() {
        super();
    }

    /**
     * <p>
     * 分页构造函数
     * </p>
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public Pagination(int current, int size) {
        this.current = current;
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
        this.pages = (int) this.total / this.size;
        if (this.total % this.size != 0) {
            this.pages++;
        }
    }

    @Override
    public String toString() {
        return "Pagination { total=" + total + " ,size=" + size + " ,pages=" + pages + " ,current=" + current + " }";
    }
}
