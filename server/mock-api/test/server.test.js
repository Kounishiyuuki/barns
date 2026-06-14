import { test, before, after } from 'node:test';
import assert from 'node:assert/strict';
import { createServer } from '../src/server.js';

let server;
let base;

before(async () => {
  server = createServer();
  await new Promise((resolve) => server.listen(0, resolve));
  const { port } = server.address();
  base = `http://localhost:${port}`;
});

after(async () => {
  await new Promise((resolve) => server.close(resolve));
});

function assertEnvelopeOk(body) {
  assert.equal(body.error, null);
  assert.ok('data' in body);
}

test('GET /health returns ok envelope', async () => {
  const res = await fetch(`${base}/health`);
  assert.equal(res.status, 200);
  const body = await res.json();
  assertEnvelopeOk(body);
  assert.equal(body.data.status, 'ok');
});

test('POST /auth/login returns mock user + token without storing body', async () => {
  const res = await fetch(`${base}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: 'demo@example.invalid' }),
  });
  assert.equal(res.status, 200);
  const body = await res.json();
  assertEnvelopeOk(body);
  assert.equal(body.data.userId, 'mock-user-001');
  assert.equal(body.data.token, 'mock-token');
});

test('GET /me returns current mock user', async () => {
  const res = await fetch(`${base}/me`);
  assert.equal(res.status, 200);
  const body = await res.json();
  assertEnvelopeOk(body);
  assert.equal(body.data.userId, 'mock-user-001');
});

for (const [path, isArray] of [
  ['/categories', true],
  ['/patterns', true],
  ['/care-guides', true],
  ['/notices', true],
  ['/company-info', false],
]) {
  test(`GET ${path} returns support data`, async () => {
    const res = await fetch(`${base}${path}`);
    assert.equal(res.status, 200);
    const body = await res.json();
    assertEnvelopeOk(body);
    if (isArray) {
      assert.ok(Array.isArray(body.data));
      assert.ok(body.data.length > 0);
    } else {
      assert.equal(typeof body.data, 'object');
    }
  });
}

test('GET /patterns/:id returns a single pattern', async () => {
  const list = await (await fetch(`${base}/patterns`)).json();
  const id = list.data[0].id;
  const res = await fetch(`${base}/patterns/${encodeURIComponent(id)}`);
  assert.equal(res.status, 200);
  const body = await res.json();
  assertEnvelopeOk(body);
  assert.equal(body.data.id, id);
});

test('GET /patterns/:id returns 404 error envelope for unknown id', async () => {
  const res = await fetch(`${base}/patterns/does-not-exist`);
  assert.equal(res.status, 404);
  const body = await res.json();
  assert.equal(body.data, null);
  assert.equal(body.error.code, 'not_found');
});

test('unknown route returns 404 error envelope', async () => {
  const res = await fetch(`${base}/care-logs`);
  assert.equal(res.status, 404);
  const body = await res.json();
  assert.equal(body.data, null);
  assert.equal(body.error.code, 'not_found');
});

test('private-data routes are not served', async () => {
  for (const path of ['/care-logs', '/consultation-drafts', '/product-items']) {
    const res = await fetch(`${base}${path}`);
    assert.equal(res.status, 404, `${path} must not be served`);
  }
});
