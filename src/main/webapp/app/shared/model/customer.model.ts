export interface ICustomer {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string | null;
  document?: string | null;
  createdDate?: Date | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public phone?: string | null,
    public document?: string | null,
    public createdDate?: Date | null,
  ) {}
}
