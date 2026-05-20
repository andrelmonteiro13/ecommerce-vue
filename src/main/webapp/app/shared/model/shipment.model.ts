import { type ICustomerOrder } from '@/shared/model/customer-order.model';
import { type ShipmentStatus } from '@/shared/model/enumerations/shipment-status.model';
export interface IShipment {
  id?: number;
  trackingNumber?: string | null;
  carrier?: string | null;
  status?: keyof typeof ShipmentStatus;
  shippedDate?: Date | null;
  deliveredDate?: Date | null;
  order?: ICustomerOrder | null;
}

export class Shipment implements IShipment {
  constructor(
    public id?: number,
    public trackingNumber?: string | null,
    public carrier?: string | null,
    public status?: keyof typeof ShipmentStatus,
    public shippedDate?: Date | null,
    public deliveredDate?: Date | null,
    public order?: ICustomerOrder | null,
  ) {}
}
