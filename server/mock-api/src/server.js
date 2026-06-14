import { createServer as createHttpServer } from 'node:http';
import { loadData, MOCK_USER, MOCK_TOKEN } from './data.js';

// Consistent response envelopes.
function success(data) {
  return { data, error: null };
}

function failure(code, message) {
  return { data: null, error: { code, message } };
}

function send(res, status, payload) {
  const body = JSON.stringify(payload);
  res.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    // CORS is enabled for LOCAL DEVELOPMENT ONLY. Production hardening
    // (auth, authz, audit logging, data isolation) is out of scope here.
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization',
  });
  res.end(body);
}

export function createServer() {
  const data = loadData();

  return createHttpServer((req, res) => {
    const method = req.method || 'GET';
    const url = new URL(req.url || '/', 'http://localhost');
    const path = url.pathname.replace(/\/+$/, '') || '/';

    // Minimal request log: method + path only. Never log bodies, headers,
    // query strings, or any personal data.
    console.log(`${method} ${path}`);

    if (method === 'OPTIONS') {
      send(res, 204, success(null));
      return;
    }

    // GET /health
    if (method === 'GET' && path === '/health') {
      send(res, 200, success({ status: 'ok' }));
      return;
    }

    // POST /auth/login -> mock auth response.
    // The request body is intentionally ignored and never stored or logged.
    if (method === 'POST' && path === '/auth/login') {
      req.resume(); // drain without reading
      send(res, 200, success({ ...MOCK_USER, token: MOCK_TOKEN }));
      return;
    }

    // GET /me -> current mock user
    if (method === 'GET' && path === '/me') {
      send(res, 200, success({ ...MOCK_USER }));
      return;
    }

    // Read-only support collections
    if (method === 'GET' && path === '/categories') {
      send(res, 200, success(data.categories));
      return;
    }

    if (method === 'GET' && path === '/patterns') {
      send(res, 200, success(data.patterns));
      return;
    }

    if (method === 'GET' && path.startsWith('/patterns/')) {
      const id = decodeURIComponent(path.slice('/patterns/'.length));
      const pattern = data.patterns.find((p) => p.id === id);
      if (!pattern) {
        send(res, 404, failure('not_found', 'Pattern not found'));
        return;
      }
      send(res, 200, success(pattern));
      return;
    }

    if (method === 'GET' && path === '/care-guides') {
      send(res, 200, success(data.careGuides));
      return;
    }

    if (method === 'GET' && path === '/notices') {
      send(res, 200, success(data.notices));
      return;
    }

    if (method === 'GET' && path === '/company-info') {
      send(res, 200, success(data.companyInfo));
      return;
    }

    // Unknown route
    send(res, 404, failure('not_found', 'Resource not found'));
  });
}
