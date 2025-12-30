import { http } from "@/utils/http.js";

/**
 * 新增图书
 * @param {Object} data
 * @param {string} data.bookName - 图书名称
 * @param {string} data.bookIntroduction - 图书简介
 * @param {string} data.cover - 封面URL
 * @param {string} data.author - 作者
 * @param {string} data.publisher - 出版社
 * @param {string} data.publishDate - 出版日期 (ISO字符串)
 * @param {string} data.isbn - ISBN
 * @param {number} data.totalStock - 总库存
 * @param {number} data.price - 价格
 * @param {string} data.language - 语言
 */
export const addBook = (data) => {
    return http.post("/book/add", data);
};

/**
 * 删除图书
 * @param {Object} data
 * @param {number|string} data.id - 图书ID
 */
export const deleteBook = (data) => {
    return http.post("/book/delete", data);
};

/**
 * 更新图书
 * @param {Object} data
 * @param {number|string} data.id - 图书ID (必填)
 * @param {number} data.status - 状态：0下架 1上架
 * @param {Object} [其他字段参照新增接口]
 */
export const updateBook = (data) => {
    return http.post("/book/update", data);
};

/**
 * 分页获取图书列表
 * @param {Object} data
 * @param {number} data.currentPage - 当前页码
 * @param {number} data.pageSize - 每页条数
 * @param {string} [data.bookName] - 图书名称筛选
 * @param {string} [data.author] - 作者筛选
 * @param {number} [data.publishDateType] - 0:之前 1:之后
 */
export const listBook = (data) => {
    return http.post("/book/list/page", data);
};