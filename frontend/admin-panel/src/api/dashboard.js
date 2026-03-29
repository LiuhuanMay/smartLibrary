import {http} from '@/utils/http.js';

export const getSummaryData = () => {
    return http.get('/dashboard/summary');
};

export const getBorrowTrend = () => {
    return http.get('/dashboard/trend');
};

export const getHotBooks = () => {
    return http.get('/dashboard/hot-books');
};
