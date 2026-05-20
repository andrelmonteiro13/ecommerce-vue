import { type ICustomer } from '@/shared/model/customer.model';

export interface IAddress {
  id?: number;
  street?: string;
  number?: string | null;
  complement?: string | null;
  district?: string | null;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  customer?: ICustomer | null;
}

export class Address implements IAddress {
  constructor(
    public id?: number,
    public street?: string,
    public number?: string | null,
    public complement?: string | null,
    public district?: string | null,
    public city?: string,
    public state?: string,
    public zipCode?: string,
    public country?: string,
    public customer?: ICustomer | null,
  ) {}
}
