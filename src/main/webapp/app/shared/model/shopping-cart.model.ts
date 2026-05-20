import { type ICustomer } from '@/shared/model/customer.model';

export interface IShoppingCart {
  id?: number;
  createdDate?: Date;
  updatedDate?: Date | null;
  customer?: ICustomer | null;
}

export class ShoppingCart implements IShoppingCart {
  constructor(
    public id?: number,
    public createdDate?: Date,
    public updatedDate?: Date | null,
    public customer?: ICustomer | null,
  ) {}
}
