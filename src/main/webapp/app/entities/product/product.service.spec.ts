import { beforeEach, describe, expect, it } from 'vitest';

import axios from 'axios';
import dayjs from 'dayjs';
import sinon from 'sinon';

import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Product } from '@/shared/model/product.model';

import ProductService from './product.service';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Product Service', () => {
    let service: ProductService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new ProductService();
      currentDate = new Date();
      elemDefault = new Product(123, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 0, 0, 'AAAAAAA', 'AAAAAAA', 0, false, currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          createdDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Product', async () => {
        const returnedFromService = {
          id: 123,
          createdDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { createdDate: currentDate, updatedDate: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Product', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Product', async () => {
        const returnedFromService = {
          name: 'BBBBBB',
          slug: 'BBBBBB',
          description: 'BBBBBB',
          price: 1,
          promotionalPrice: 1,
          sku: 'BBBBBB',
          imageUrl: 'BBBBBB',
          stock: 1,
          active: true,
          createdDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = { createdDate: currentDate, updatedDate: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Product', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Product', async () => {
        const patchObject = { promotionalPrice: 1, updatedDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...new Product() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { createdDate: currentDate, updatedDate: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Product', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Product', async () => {
        const returnedFromService = {
          name: 'BBBBBB',
          slug: 'BBBBBB',
          description: 'BBBBBB',
          price: 1,
          promotionalPrice: 1,
          sku: 'BBBBBB',
          imageUrl: 'BBBBBB',
          stock: 1,
          active: true,
          createdDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { createdDate: currentDate, updatedDate: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Product', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Product', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Product', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
