import { type IAddress } from '@/shared/model/address.model';
import { type ICustomer } from '@/shared/model/customer.model';
import { type CustomerOrderStatus } from '@/shared/model/enumerations/customer-order-status.model';
import { type IUser } from '@/shared/model/user.model';

export interface ICustomerOrder {
  id?: number;
  orderNumber?: string;
  orderDate?: Date;
  status?: keyof typeof CustomerOrderStatus;
  subtotal?: number;
  discount?: number | null;
  shippingCost?: number | null;
  totalPrice?: number;
  notes?: string | null;
  customer?: ICustomer | null;
  user?: IUser | null;
  shippingAddress?: IAddress | null;
}

export class CustomerOrder implements ICustomerOrder {
  constructor(
    public id?: number,
    public orderNumber?: string,
    public orderDate?: Date,
    public status?: keyof typeof CustomerOrderStatus,
    public subtotal?: number,
    public discount?: number | null,
    public shippingCost?: number | null,
    public totalPrice?: number,
    public notes?: string | null,
    public customer?: ICustomer | null,
    public user?: IUser | null,
    public shippingAddress?: IAddress | null,
  ) {}
}
