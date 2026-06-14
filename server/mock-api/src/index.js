import { createServer } from './server.js';

const PORT = Number(process.env.PORT) || 8787;

const server = createServer();
server.listen(PORT, () => {
  console.log(`barns mock API listening on http://localhost:${PORT}`);
});
