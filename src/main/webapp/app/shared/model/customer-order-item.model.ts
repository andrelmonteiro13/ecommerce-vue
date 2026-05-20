import { type ICustomerOrder } from '@/shared/model/customer-order.model';
import { type IProduct } from '@/shared/model/product.model';

export interface ICustomerOrderItem {
  id?: number;
  quantity?: number;
  unitPrice?: number;
  totalPrice?: number;
  order?: ICustomerOrder | null;
  product?: IProduct | null;
}

export class CustomerOrderItem implements ICustomerOrderItem {
  constructor(
    public id?: number,
    public quantity?: number,
    public unitPrice?: number,
    public totalPrice?: number,
    public order?: ICustomerOrder | null,
    public product?: IProduct | null,
  ) {}
}
