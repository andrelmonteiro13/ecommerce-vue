import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import CustomerOrderService from '@/entities/customer-order/customer-order.service';
import ProductService from '@/entities/product/product.service';
import AlertService from '@/shared/alert/alert.service';

import CustomerOrderItemUpdate from './customer-order-item-update.vue';
import CustomerOrderItemService from './customer-order-item.service';

type CustomerOrderItemUpdateComponentType = InstanceType<typeof CustomerOrderItemUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const customerOrderItemSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CustomerOrderItemUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('CustomerOrderItem Management Update Component', () => {
    let comp: CustomerOrderItemUpdateComponentType;
    let customerOrderItemServiceStub: SinonStubbedInstance<CustomerOrderItemService>;

    beforeEach(() => {
      route = {};
      customerOrderItemServiceStub = sinon.createStubInstance<CustomerOrderItemService>(CustomerOrderItemService);
      customerOrderItemServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          customerOrderItemService: () => customerOrderItemServiceStub,
          customerOrderService: () =>
            sinon.createStubInstance<CustomerOrderService>(CustomerOrderService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          productService: () =>
            sinon.createStubInstance<ProductService>(ProductService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(CustomerOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.customerOrderItem = customerOrderItemSample;
        customerOrderItemServiceStub.update.resolves(customerOrderItemSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(customerOrderItemServiceStub.update.calledWith(customerOrderItemSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        customerOrderItemServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CustomerOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.customerOrderItem = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(customerOrderItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        customerOrderItemServiceStub.find.resolves(customerOrderItemSample);
        customerOrderItemServiceStub.retrieve.resolves([customerOrderItemSample]);

        // WHEN
        route = {
          params: {
            customerOrderItemId: `${customerOrderItemSample.id}`,
          },
        };
        const wrapper = shallowMount(CustomerOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.customerOrderItem).toMatchObject(customerOrderItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        customerOrderItemServiceStub.find.resolves(customerOrderItemSample);
        const wrapper = shallowMount(CustomerOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
