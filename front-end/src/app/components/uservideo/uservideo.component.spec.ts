import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UservideoComponent } from './uservideo.component';

describe('UservideoComponent', () => {
  let component: UservideoComponent;
  let fixture: ComponentFixture<UservideoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UservideoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UservideoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
