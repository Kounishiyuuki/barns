import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, join } from 'node:path';

const here = dirname(fileURLToPath(import.meta.url));
// server/mock-api/src -> repo/shared/mock-data
const MOCK_DATA_DIR = join(here, '..', '..', '..', 'shared', 'mock-data');

// Allowlist: the mock API may serve ONLY these static support datasets.
// Customer-side private data (registered items, care logs, consultation
// drafts, photos, addresses, phone numbers, personal notes) is intentionally
// excluded and must never be served, received, or stored here.
const ALLOWED_FILES = {
  categories: 'categories.json',
  patterns: 'patterns.json',
  careGuides: 'care-guides.json',
  notices: 'notices.json',
  companyInfo: 'company-info.json',
};

function loadJson(fileName) {
  const raw = readFileSync(join(MOCK_DATA_DIR, fileName), 'utf8');
  return JSON.parse(raw);
}

// Loaded once at startup; the server is read-only so this never mutates.
export function loadData() {
  const data = {};
  for (const [key, file] of Object.entries(ALLOWED_FILES)) {
    data[key] = loadJson(file);
  }
  return data;
}

// Fixed mock identity for MVP flow confirmation only. Not a real credential.
export const MOCK_USER = {
  userId: 'mock-user-001',
  displayName: 'Demo User',
};

export const MOCK_TOKEN = 'mock-token';
