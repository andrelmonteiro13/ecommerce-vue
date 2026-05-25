import { type IProduct } from '@/shared/model/product.model';

export default class PublicProductService {
  async retrieve(): Promise<IProduct[]> {
    const response = await fetch(`${window.location.origin}/api/public/products`, {
      method: 'GET',
      headers: {
        Accept: 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`Erro ao buscar produtos: ${response.status}`);
    }

    return response.json();
  }
}
