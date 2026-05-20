import { type IProduct } from '@/shared/model/product.model';
import { type IShoppingCart } from '@/shared/model/shopping-cart.model';

export interface IShoppingCartItem {
  id?: number;
  quantity?: number;
  unitPrice?: number;
  cart?: IShoppingCart | null;
  product?: IProduct | null;
}

export class ShoppingCartItem implements IShoppingCartItem {
  constructor(
    public id?: number,
    public quantity?: number,
    public unitPrice?: number,
    public cart?: IShoppingCart | null,
    public product?: IProduct | null,
  ) {}
}
