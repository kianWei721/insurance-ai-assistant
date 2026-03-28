import express from 'express';
import documentRoutes from './routes/documentRoutes';

const app = express();

app.use(express.json());
app.use('/api/documents', documentRoutes);

export default app;
