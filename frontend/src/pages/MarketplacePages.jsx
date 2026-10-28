import React, { useEffect, useState } from 'react';
import AppShell from '../components/layout/AppShell.jsx';
import { useNavigate } from '../store/appContext.js';
import { getAuction, getAuctions, getMarket, getMarkets } from '../api/marketplaceService.js';
import '../styles/marketplacePages.css';

const decode = (value) => { try { return decodeURIComponent(value); } catch { return ''; } };
function Heading({ title, description }) { return <header className="mp-heading"><div><p className="mp-eyebrow">ROUP MARKETPLACE</p><h1>{title}</h1><p>{description}</p></div></header>; }
function LoadState({ state, error, retry, empty }) { if (state === 'loading') return <p role="status">Loading data from Roup…</p>; if (state === 'error') return <div role="alert"><p>Data is unavailable: {error}</p><button className="mp-button mp-button-secondary" onClick={retry}>Try again</button></div>; if (state === 'ready' && empty) return <p role="status">No records are currently available.</p>; return null; }
function useRemote(load, key) { const [data, setData] = useState(null); const [state, setState] = useState('loading'); const [error, setError] = useState(''); const refresh = async () => { setState('loading'); setError(''); try { setData(await load()); setState('ready'); } catch (e) { setError(e.message); setState('error'); } }; useEffect(() => { refresh(); }, [key]); return { data, state, error, refresh }; }

export function MarketsPage({ path = '/markets' }) {
  const navigate = useNavigate(); const id = path.startsWith('/markets/') ? decode(path.slice(9)) : '';
  const result = useRemote(() => id ? getMarket(id) : getMarkets(), id || 'markets');
  const list = Array.isArray(result.data) ? result.data : [];
  return <AppShell currentPath="/markets" pageClass="marketplace-route-content"><div className="mp-page"><Heading title={id ? 'Market details' : 'Markets'} description="Market configuration and details provided by the backend."/><LoadState state={result.state} error={result.error} retry={result.refresh} empty={id ? !result.data : !list.length}/>{result.state === 'ready' && (id ? result.data && <MarketRecord market={result.data}/> : list.length > 0 && <div className="mp-market-cards">{list.map((market) => <article className="mp-panel" key={market.assetId}><MarketRecord market={market}/><button className="mp-button mp-button-secondary" onClick={() => navigate(`/markets/${encodeURIComponent(market.assetId)}`)}>Details</button></article>)}</div>)}</div></AppShell>;
}
function MarketRecord({ market }) { return <><div className="mp-panel-heading"><div><h2>{market.assetName || 'Unnamed asset'}</h2><p>{market.assetDescription || 'No description supplied.'}</p></div><span>{market.isTradingActive ? 'Trading active' : 'Trading inactive'}</span></div><div className="mp-detail-pairs"><span>Asset ID <b>{market.assetId}</b></span><span>Face value <b>{market.originalFaceValue ?? 'Unavailable'}</b></span><span>Maximum resale price <b>{market.maximumResalePrice ?? 'Unavailable'}</b></span><span>Quantity <b>{market.totalOriginalQuantity ?? 'Unavailable'}</b></span></div></>; }

export function AuctionsPage({ path = '/auctions' }) {
  const navigate = useNavigate(); const id = path.startsWith('/auctions/') ? decode(path.slice(10)) : '';
  const result = useRemote(() => id ? getAuction(id) : getAuctions(), id || 'auctions'); const list = Array.isArray(result.data) ? result.data : [];
  return <AppShell currentPath="/auctions" pageClass="marketplace-route-content"><div className="mp-page"><Heading title={id ? 'Auction details' : 'Dutch auctions'} description="Auction data returned by the Roup auction service. Purchase actions are not available here because acceptance is asynchronous and buyer identity is not implemented safely by the current API."/><LoadState state={result.state} error={result.error} retry={result.refresh} empty={id ? !result.data : !list.length}/>{result.state === 'ready' && (id ? result.data && <AuctionRecord auction={result.data}/> : list.length > 0 && <div className="mp-market-cards">{list.map((auction) => <article className="mp-panel" key={auction.id}><AuctionRecord auction={auction}/><button className="mp-button mp-button-secondary" onClick={() => navigate(`/auctions/${encodeURIComponent(auction.id)}`)}>Details</button></article>)}</div>)}</div></AppShell>;
}
function AuctionRecord({ auction }) { return <div className="mp-panel-heading"><div><h2>{auction.assetName || 'Unnamed asset'}</h2><p>{auction.assetDiscription || 'No description supplied.'}</p><p>Auction ID: {auction.id}</p><p>Status: {auction.isActive ? 'Active' : 'Inactive'}{auction.endTime ? ` · Ends ${new Date(auction.endTime).toLocaleString()}` : ''}</p></div><span>Current price: {auction.currentPrice ?? 'Unavailable'}</span></div>; }

function UnavailablePage({ path, title, feature }) { return <AppShell currentPath={path} pageClass="marketplace-route-content"><div className="mp-page"><Heading title={title} description={`${feature} is unavailable because this repository does not currently provide a persistent, authenticated backend API for it.`}/><section className="mp-panel"><h2>No account data shown</h2><p>This page does not store or simulate ${feature.toLowerCase()} in the browser. It will show records when a real backend implementation is available.</p></section></div></AppShell>; }
export function OrdersPage() { return <UnavailablePage path="/orders" title="My orders" feature="Order management"/>; }
export function ListingsPage() { return <UnavailablePage path="/listings" title="My listings" feature="Seller listings"/>; }
export function WatchlistPage() { return <UnavailablePage path="/watchlist" title="Your watchlist" feature="Account-backed watchlists"/>; }
