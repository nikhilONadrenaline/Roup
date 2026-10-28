import { apiRequest } from './apiClient.js';

export const getMarkets = () => apiRequest('/api/secondary-markets/active-markets');
export const getMarket = (assetId) => apiRequest(`/api/secondary-markets/${encodeURIComponent(assetId)}`);
export const getAuctions = () => apiRequest('/api/dutch-auctions');
export const getAuction = (auctionId) => apiRequest(`/api/dutch-auctions/${encodeURIComponent(auctionId)}`);
