export interface ICategory {
  id?: number;
  name?: string;
  description?: string | null;
  active?: boolean;
}

export class Category implements ICategory {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public active?: boolean,
  ) {
    this.active = this.active ?? false;
  }
}
