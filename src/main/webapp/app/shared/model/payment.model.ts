import { type ICustomerOrder } from '@/shared/model/customer-order.model';
import { type PaymentMethod } from '@/shared/model/enumerations/payment-method.model';
import { type PaymentStatus } from '@/shared/model/enumerations/payment-status.model';
export interface IPayment {
  id?: number;
  method?: keyof typeof PaymentMethod;
  status?: keyof typeof PaymentStatus;
  amount?: number;
  transactionCode?: string | null;
  paidDate?: Date | null;
  order?: ICustomerOrder | null;
}

export class Payment implements IPayment {
  constructor(
    public id?: number,
    public method?: keyof typeof PaymentMethod,
    public status?: keyof typeof PaymentStatus,
    public amount?: number,
    public transactionCode?: string | null,
    public paidDate?: Date | null,
    public order?: ICustomerOrder | null,
  ) {}
}
