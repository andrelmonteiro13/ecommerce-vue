import { type ICategory } from '@/shared/model/category.model';

export interface IProduct {
  id?: number;
  name?: string;
  slug?: string;
  description?: string | null;
  price?: number;
  promotionalPrice?: number | null;
  sku?: string | null;
  imageUrl?: string | null;
  stock?: number;
  active?: boolean;
  createdDate?: Date | null;
  updatedDate?: Date | null;
  category?: ICategory | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public slug?: string,
    public description?: string | null,
    public price?: number,
    public promotionalPrice?: number | null,
    public sku?: string | null,
    public imageUrl?: string | null,
    public stock?: number,
    public active?: boolean,
    public createdDate?: Date | null,
    public updatedDate?: Date | null,
    public category?: ICategory | null,
  ) {
    this.active = this.active ?? false;
  }
}
