import { Router } from 'express';
import multer from 'multer';
import { createDocumentController } from '../controllers/documentController';

const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 50 * 1024 * 1024 }, // 50 MB
});

const router = Router();
const controller = createDocumentController();

/**
 * POST /api/documents/pdf/import
 */
router.post(
  '/pdf/import',
  upload.single('file'),
  (req, res) => controller.importPdf(req, res)
);

export default router;
